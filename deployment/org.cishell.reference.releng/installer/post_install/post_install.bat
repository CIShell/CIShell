move "$INSTALL_PATH\${archivePrefix}" "$INSTALL_PATH\${archivePrefix}_install"
xcopy /E "$INSTALL_PATH\${archivePrefix}_install\*" "$INSTALL_PATH"
rmdir /S /Q "$INSTALL_PATH\${archivePrefix}_install"
