cd /home/mnaeem/workspace/sampling
mvn clean
mvn package
cd /home/mnaeem/workspace/sampling/target/classes
export CLASSPATH=$CLASSPATH:/home/mnaeem/workspace/sampling/lib/commons-io-2.4.jar
export CLASSPATH=$CLASSPATH:/home/mnaeem/workspace/sampling/lib/commons-lang3-3.3.2.jar
export CLASSPATH=$CLASSPATH:/home/mnaeem/workspace/sampling/lib/commons-math3-3.5.jar

java oversampling.Sampling
cd /home/mnaeem/workspace/sampling
