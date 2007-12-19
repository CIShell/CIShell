#!/bin/sh

mv "$INSTALL_PATH/${archivePrefix}" "$INSTALL_PATH/${archivePrefix}_install"
mv "$INSTALL_PATH/${archivePrefix}_install"/* "$INSTALL_PATH"
mv "$INSTALL_PATH/${archivePrefix}_install"/.* "$INSTALL_PATH"
rmdir "$INSTALL_PATH/${archivePrefix}_install"
