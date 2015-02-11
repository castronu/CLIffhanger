#!/bin/sh

# This is the CLIffhanger install script!
#
# It is based on Meteor installation script: https://www.meteor.com/
run_it () {

# Install Meteor:

curl https://install.meteor.com/ | sh

# This always does a clean install of the latest version of CLIffhanger.

RELEASE="1.2.0-SNAPSHOT"


# Now, on to the actual installer!

## NOTE sh NOT bash. This script should be POSIX sh only, since we don't
## know what shell the user has. Debian uses 'dash' for 'sh', for
## example.

PREFIX="/usr/local"

set -e
set -u

# Let's display everything on stderr.
exec 1>&2


UNAME=$(uname)
if [ "$UNAME" != "Linux" -a "$UNAME" != "Darwin" ] ; then
    echo "Sorry, this OS is not supported yet."
    exit 1
fi


if [ "$UNAME" = "Darwin" ] ; then
  ### OSX ###
  if [ "i386" != "$(uname -p)" -o "1" != "$(sysctl -n hw.cpu64bit_capable 2>/dev/null || echo 0)" ] ; then
    # Can't just test uname -m = x86_64, because Snow Leopard can
    # return other values.
    echo "Only 64-bit Intel processors are supported at this time."
    exit 1
  fi
  PLATFORM="os.osx.x86_64"
elif [ "$UNAME" = "Linux" ] ; then
  ### Linux ###
  LINUX_ARCH=$(uname -m)
  if [ "${LINUX_ARCH}" = "i686" ] ; then
    PLATFORM="os.linux.x86_32"
  elif [ "${LINUX_ARCH}" = "x86_64" ] ; then
    PLATFORM="os.linux.x86_64"
  else
    echo "Unusable architecture: ${LINUX_ARCH}"
    echo "Meteor only supports i686 and x86_64 for now."
    exit 1
  fi
fi

trap "echo Installation failed." EXIT

# If you already have a tropohouse/warehouse, we do a clean install here:
if [ -e "$HOME/.cliffhanger" ]; then
  echo "Removing your existing CLIffhanger installation."
  rm -rf "$HOME/.cliffhanger"
fi

TARBALL_URL="http://www.diegocastronuovo.com/cliffhanger/cliffhanger-${RELEASE}-bin.tar.gz"

INSTALL_TMPDIR="$HOME/.cliffhanger-install-tmp"
rm -rf "$INSTALL_TMPDIR"
mkdir "$INSTALL_TMPDIR"
echo "Downloading CLIffhanger distribution"
curl --progress-bar --fail "$TARBALL_URL" | tar -xzf - -C "$INSTALL_TMPDIR" -o
# bomb out if it didn't work, eg no net
test -x "${INSTALL_TMPDIR}/cliffhanger-${RELEASE}"
mv "${INSTALL_TMPDIR}/cliffhanger-${RELEASE}" "$HOME/.cliffhanger"
rm -rf "${INSTALL_TMPDIR}"
# just double-checking :)
test -x "$HOME/.cliffhanger"



echo
echo "CLIffhanger ${RELEASE} has been installed in your home directory (~/.cliffhanger)."

LAUNCHER="$HOME/.cliffhanger/CLIffhanger.sh"


if ln -s "$LAUNCHER" "$PREFIX/bin/cliffhanger"  >/dev/null 2>&1; then
  echo "Writing a launcher script to $PREFIX/bin/cliffhanger for your convenience."
  cat <<"EOF"

To get started fast:

  $ cliffhanger [xmlConfig]

Or see the docs at:

  www.xyz.com

EOF
elif type sudo >/dev/null 2>&1; then
  echo "Writing a launcher script to $PREFIX/bin/cliffhanger for your convenience."
  echo "This may prompt for your password."

  # New macs (10.9+) don't ship with /usr/local, however it is still in
  # the default PATH. We still install there, we just need to create the
  # directory first.
  # XXX this means that we can run sudo too many times. we should never
  #     run it more than once if it fails the first time
  if [ ! -d "$PREFIX/bin" ] ; then
      sudo mkdir -m 755 "$PREFIX" || true
      sudo mkdir -m 755 "$PREFIX/bin" || true
  fi

  if sudo ln -s  "$LAUNCHER" "$PREFIX/bin/cliffhanger"  ; then
    cat <<"EOF"

To get started fast:

  $ cliffhanger [xmlConfig]

Or see the docs at:

  www.xyz.com

EOF
  else
    cat <<EOF

Couldn't write the launcher script. Please either:

  (1) Run the following as root:
        cp "$LAUNCHER" /usr/bin/cliffhanger
  (2) Add "\$HOME/.cliffhanger" to your path, or
  (3) Rerun this command to try again.

Then to get started, take a look at 'meteor --help' or see the docs at
www.xyz.com
EOF
  fi
else
  cat <<EOF

Now you need to do one of the following:

  (1) Add "\$HOME/.cliffhanger" to your path, or
  (2) Run this command as root:
        cp "$LAUNCHER" /usr/bin/cliffhanger

Then to get started, take a look at
www.xyz.com
EOF
fi


trap - EXIT

}

run_it
