if [ $1  = mono ]
then
    echo "compiling classes..."
    javac src/stream/*.java
    echo "classes compiled."
    echo "running EchoServer..."
    java src/stream/EchoServer
fi
if [ $1 = multi ]
    echo "compiling classes..."
    javac src/stream/*.java
    echo "classes compiled."
    echo "running EchoServerMultiThreaded..."
    java src/stream/EchoServerMultiThreaded
fi
if [ $1 = chat ]
    echo "compiling classes..."
    javac src/stream/*.java
    echo "classes compiled."
    echo "running ChatServer..."
    java src/stream/ChatServer
fi




