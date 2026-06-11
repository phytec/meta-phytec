#!/usr/bin/env python3
#
# Copyright 2026, PHYTEC Messtechnik GmbH
# Author: Wadim Egorov <w.egorov@phytec.de>
#
# Update linux, u-boot and barebox recipes to the newest tag
# on their integration branches.

import argparse
import os
import re
import shutil
import subprocess
import sys
import tempfile

SRCREV_RE = re.compile(r'^SRCREV = "[0-9a-fA-F]+"$', re.MULTILINE)
GIT_URL_RE = re.compile(r'^GIT_URL = "([^"]+)"$', re.MULTILINE)

def git(*args, **kwargs):
    return subprocess.run(
        ["git", *args], check=True, stdout=subprocess.PIPE,
        stderr=subprocess.PIPE, encoding="utf-8", **kwargs,
    ).stdout


def repo_root():
    return git("rev-parse", "--show-toplevel").strip()


def parse_recipe_filename(path):
    """Split a recipe path into (pn, pv), e.g. linux-phytec-ti, 6.18.13-...-phy3."""
    name = os.path.basename(path)
    if not name.endswith(".bb"):
        raise ValueError(f"not a .bb recipe: {path}")
    pn, sep, pv = name.removesuffix(".bb").partition("_")
    if not sep:
        raise ValueError(f"recipe has no version in its filename: {name}")
    return pn, pv


def integration_branch(pv):
    if "-phy" in pv:
        return "v" + pv.rstrip("0123456789")
    return "v" + pv + "-phy"


def git_url(raw, pn):
    """Turn the bitbake GIT_URL into a fetchable URL. GIT_URL always
    points to github with protocol=https."""
    raw = raw.replace("${BPN}", pn)
    return "https://" + raw.split(";")[0].removeprefix("git://")


def ls_remote_tags(url, branch):
    out = git("ls-remote", "--tags", url, branch + "*")
    tags = {}
    for line in out.splitlines():
        sha, _, ref = line.partition("\t")
        if not ref.startswith("refs/tags/"):
            continue
        name = ref[len("refs/tags/"):]
        name = name.removesuffix("^{}")
        tags[name] = sha
    return tags


def newest_tag(tags, branch, current_phy):
    phy_re = re.compile(r"^%s(\d+)$" % re.escape(branch))

    newer = []
    for name in tags:
        match = phy_re.match(name)
        if not match:
            # not a -phyN tag for this branch
            continue
        phy = int(match.group(1))
        if phy > current_phy:
            newer.append((phy, name))

    if not newer:
        return None
    return max(newer)


def changelog(url, oldtag, newtag):
    tmp = tempfile.mkdtemp()
    try:
        git("init", "-q", tmp)
        git("-C", tmp, "remote", "add", "origin", url)
        git("-C", tmp, "fetch", "-q", "--filter=tree:0", "--depth=200",
            "origin", "tag", oldtag, "tag", newtag)
        out = git("-C", tmp, "log", "--format=%s",
                  "%s..%s" % (oldtag, newtag))
    finally:
        shutil.rmtree(tmp, ignore_errors=True)
    subjects = []
    for line in out.splitlines():
        if line:
            subjects.append(line)
    return subjects


def commit_message(rel, pn, new_pv, oldtag, subjects):
    topdir = rel.split(os.sep, 1)[0]
    subject = f"{topdir}: {pn}: Update to v{new_pv}"
    name = git("config", "user.name").strip()
    email = git("config", "user.email").strip()
    lines = [subject, "", f"Changes since {oldtag}:"]
    lines += [f"    {s}" for s in subjects]
    lines += ["", f"Signed-off-by: {name} <{email}>"]
    return "\n".join(lines) + "\n"


def update_recipe(recipe_path, root, dry_run):
    # Parse the filename: package name, tag, and branch
    rel = os.path.relpath(recipe_path, root)
    pn, pv = parse_recipe_filename(recipe_path)

    phy_m = re.search(r"\d+$", pv)
    if not phy_m:
        print(f"{rel}: no trailing -phyN version, nothing to update")
        return False
    current_phy = int(phy_m.group())
    base_pv = pv[:phy_m.start()]
    branch = integration_branch(pv)

    # Read the recipe; grab its upstream URL and check it has one SRCREV
    text = open(recipe_path, encoding="utf-8").read()
    url_m = GIT_URL_RE.search(text)
    if not url_m:
        raise ValueError(f"no GIT_URL found in {rel}")
    if len(SRCREV_RE.findall(text)) != 1:
        raise ValueError(f"expected exactly one bare SRCREV line in {rel}")
    url = git_url(url_m.group(1), pn)

    # Ask the remote for the newest -phyN tag past the current one
    tags = ls_remote_tags(url, branch)
    newest = newest_tag(tags, branch, current_phy)
    if newest is None:
        print(f"{rel}: up to date (v{pv})")
        return False

    # Work out the new version, SRCREV, and renamed recipe path
    new_phy, newtag = newest
    new_pv = "%s%d" % (base_pv, new_phy)
    oldtag = "v" + pv
    new_srcrev = tags[newtag]
    new_path = os.path.join(os.path.dirname(recipe_path), "%s_%s.bb" % (pn, new_pv))
    new_rel = os.path.relpath(new_path, root)

    # Report the bump and the upstream changelog
    print(f"{rel}: v{pv} -> v{new_pv} ({new_srcrev[:12]})")
    subjects = changelog(url, oldtag, newtag)
    for s in subjects:
        print(f"    {s}")

    # Dry run -> stop here
    if dry_run:
        print(f"    [dry-run] would rename to {new_rel} and create a commit")
        return True

    # Local edits to the recipe would silently become part of the bump
    if git("-C", root, "status", "--porcelain", "--", rel).strip():
        raise ValueError(f"{rel} has uncommitted changes, commit or stash them first")

    # Rename the recipe & update its SRCREV
    git("-C", root, "mv", rel, new_rel)
    new_text = SRCREV_RE.sub('SRCREV = "%s"' % new_srcrev, text)
    with open(new_path, "w", encoding="utf-8") as fh:
        fh.write(new_text)
    git("-C", root, "add", new_rel)

    # Commit only the rename so anything else staged stays out
    msg = commit_message(new_rel, pn, new_pv, oldtag, subjects)
    git("-C", root, "commit", "-F", "-", "--", rel, new_rel, input=msg)

    print(f"    committed: {msg.splitlines()[0]}")
    return True


def main():
    ap = argparse.ArgumentParser(
        description="Update a recipe to the newest tag on its integration branch.")
    ap.add_argument("recipe", help="recipe .bb file to update")
    ap.add_argument("-n", "--dry-run", action="store_true",
                    help="only report what would change")
    args = ap.parse_args()

    path = os.path.abspath(args.recipe)
    if not os.path.isfile(path):
        print(f"{args.recipe}: not found", file=sys.stderr)
        return 1
    try:
        update_recipe(path, repo_root(), args.dry_run)
    except Exception as exc:
        detail = getattr(exc, "stderr", "") or ""
        print(f"error: {exc} {detail.strip()}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
