# CIShell

This is the core of the CIShell framework, see http://cishell.org/

A warning: this framework is surprisingly big, mainly because of the
release engineering components in deployment/.  You can expect about
1.5 GB of disk usage from the git repository and the working tree.

For build instructions, see [the instructions for building Sci2](https://github.com/cns-iu/cishell-applications/blob/master/README.md).

## Edit May 29 2018
Removed GUI components from CIShell, renamed the new repo as cishell-core.
1.  Removed the following:
  a.  deployment
  b.  gui
  c.  templates
2.  Shifted the removed items to cishell-ref-gui branch.
3.  If you need a CIShell GUI, checkout cishell-ref-gui branch.
    Build CIShell-core first then build cishell-ref-gui.
