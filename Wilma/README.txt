INSTALLING WILMA:
  Tim Dwyer - September 2001


This is actually really easy; however, in trying to be platform independant,
and not making any assumptions about who you are, this README's become quite
verbose.

STEP 1
------

first install J2RE V1.4 or greater
  
  get it from: http://java.sun.com/j2se/
  
STEP 2
------

then install the runtime for Java3D V1.3.1 or greater
(OpenGL version seems more reliable than DirectX)
  get it from: 
    http://java.sun.com/products/java-media/3D/download.html (Win/Solaris)
    http://blackdown.org (Linux)
    
NOTE: Debian packages for both of the above are also available from 
  ftp://ftp.tux.org/pub/java/debian/dists/woody/non-free/binary-i386

  Other blackdown mirrors may also have the JDK and Java3D but many didn't seem to be up to date.

STEP 3
------

On Windows:
  unzip the Wilma_{VERSION}.zip file somewhere convenient
On *n*x:
  tar xzvf Wilma_{VERSION}.tar.gz

STEP 4
------

set JAVA_HOME to the place where your jdk installation lives:

  Windows: Start Menu
             -->Settings
               -->Control Panel
                 -->System
                   -->Advanced
                     -->Environment Variables

           Possible values (check your own setup!)
             c:\jdk1.4.2 
                - if you installed the full J2SDK (JDK) V1.4.2
             c:\Program Files\Java\jre1.5.0
                - if you installed the run time environment
             
  Linux/Solaris:
    export JAVA_HOME=Where-ever you installed java
    
    possible values:
      /usr/local/jdk1.4.2
      /usr/lib/j2sdk1.4.2 (this is where Debian usually puts it)

STEP 5
------

In the same way that you set JAVA_HOME now set the WILMA_HOME environment
variable to the full path of the "Wilma" directory extracted from the zip

STEP 6
------
build: (If you have only downloaded the source)

from the Wilma dir type: 

./build

STEP 7
------
run:

Wilma/bin/wilma (linux/solaris/cygwin)
Wilma/bin/wilma.bat (Windows)

ENJOY!
