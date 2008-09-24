/***********************************************************************
* Copyright (c) 2005, 2007 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials 
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     IBM Corporation - initial API and implementation
* Martin Oberhuber (Wind River) - [183137] liblocalfile for solaris-sparc
***********************************************************************/

/* Use this directive when introducing platform-specific code in localfile.c */
#ifndef LINUX
#define LINUX
#endif

/* Linux supports reading symbolic links */
#ifndef EFS_SYMLINK_SUPPORT
#define EFS_SYMLINK_SUPPORT
#endif
#include <limits.h>
#include <unistd.h>
