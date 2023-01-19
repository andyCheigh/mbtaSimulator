# MBTA Simulator
This project simulates how Boston MBTA operated using multithreading techniques because trains and passengers don’t move one at time but rather in concurrent motion. Java’s thread features are applied on each of the train and each of the passengers based on train line data and passenger’s journey data, which are in JSON format. (Key: train line, Value: [stations], Key: person name, Value: [stations]) 

## Building & Running Code
This project utilizes JSON to receive passenger and train information.

Run javac *.java with Gson and JUnit in classpath:
```sh
./build
```
Run java Sim config_filename with Gson in classpath:
```sh
./sim config_filename
```
Run java Verify config_filename log_filename with Gson in classpath:
```sh
./verify config_filename log_filename
```
Run java org.junit.runner.JUnitCore test_classname with Gson and Junit in classpath:
```sh
./test test_classname
```

## Concurrent Behavior
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
