# mbtaSimulator

## Building & Running Code
This project utilizes JSON to receive passenger and train information.

./build - run javac *.java with Gson and JUnit in classpath
./sim config_filename - run java Sim config_filename with Gson in classpath
./verify config_filename log_filename - run java Verify config_filename log_filename with Gson in classpath
./test test_classname - run java org.junit.runner.JUnitCore test_classname with Gson and Junit in classpath


## Threads & Locks Explanation:
First, I made new threads for each passenger. Since passengers' behaviors
are independent of each other, it makes sense to have separate threads. For
each passenger, I run a while loop until each of them has arrived at their
destination.

Then, I made new threads for each train. Since trains run independently
unless a station already has another train, it makes sense to have separate
threads. I attempted synchronized block to avoid situation where more than
one train arrives at the same stop. But since this was not working, I had to
comment the code out.

At the end, I joined all the train threads together as they will be happening
concurrently.