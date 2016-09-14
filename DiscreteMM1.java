import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

/**
 * This class represents the methods used to get the time precision of a
 * simulation of mm1 queue
 * 
 * @author Madhuri Gurumurthy
 *
 */
public class DiscreteMM1 {
// lambda-arrival rate, mu-mean service rate
private double lambda, mu;
StopWatch stopWatch = new StopWatch();
private Random rand;

/**
* @param lambda
* @param mu
*/
public DiscreteMM1(double lambda, double mu) {
rand = new Random();
this.lambda = lambda;
this.mu = mu;
}

/**
* Sample from exponential distribution with parameter mu
* 
* @param mu
*            the parameter of the exponential distribution
* @return a sample from the exp. dist.
*/
public double exponential(double mu) {
return -Math.log(rand.nextDouble()) / mu;
}

/**
* To calculate interarrival time, which is exponential
* 
* @return interarrivalTime.
*/
private double interarrivalTime() {
return exponential(lambda);
}

/**
* To calculate service time, which is exponential
* 
* @return serviceTime.
*/
private double serviceTime() {
return exponential(mu);
}

/**
* method to simulate the mm1 queue
* 
* @param N
* @return
*/
public List simulate(int N) {
List<Long> timeTaken = new ArrayList<Long>();
// initialisation
int jobNumber = 0; // job number
double waitingTimeOfAJob = 0.0; // waiting time of job n
// we assume that initially the system is empty
double sumOfWaitingTime = 0.0; // sum of the waiting times
double sumOfSquaresOfWaitingTimes = 0.0; // sum of squares of the
// waiting times
double sumOfResponseTime = 0.0; // sum of the response times
double sumOfSquareOfResponseTimes = 0.0; // sum of squares of the
// response times

long beginTime = System.nanoTime();

stopWatch.reset();
stopWatch.start();

while (jobNumber < N) {
double interArrivalTimeOfCustomer = interarrivalTime(); // interarrival
// time
// of
// customer
// n
double serviceTimeOfCustomer = serviceTime(); // service time of
// customer n
double responseTimeOfACustomer = waitingTimeOfAJob
+ serviceTimeOfCustomer; // sojourn time of customer n
sumOfResponseTime = sumOfResponseTime + responseTimeOfACustomer;
sumOfSquareOfResponseTimes = sumOfSquareOfResponseTimes
+ responseTimeOfACustomer * responseTimeOfACustomer;
sumOfWaitingTime = sumOfWaitingTime + waitingTimeOfAJob; // add
// waiting
// time
// of
// customer
// n
sumOfSquaresOfWaitingTimes = sumOfSquaresOfWaitingTimes
+ waitingTimeOfAJob * waitingTimeOfAJob;
waitingTimeOfAJob = Math.max(waitingTimeOfAJob
+ serviceTimeOfCustomer - interArrivalTimeOfCustomer, 0); // waiting
// time
// of
// customer
// n+1
jobNumber++;
}
double meanWaitingTime = sumOfWaitingTime / N;

double rho = lambda / mu;
double wt = rho / (mu - lambda);

stopWatch.stop();

timeTaken.add(0, System.nanoTime() - beginTime);

timeTaken.add(1, stopWatch.getNanoTime());

return timeTaken;

}

public static void main(String[] arg) {
long[] timeTakenBySystemNano = new long[500];
long[] timeTakenByStopWatch = new long[500];
long systemMean = 0;
long stopWatchMean = 0;
double lambda = 0.05;
for (int i = 0; i < 500; i++) {
if (lambda <= 0.95) {
lambda += 0.05;
}
if (lambda > 0.95) {
lambda = 0.05;
}
DiscreteMM1 s = new DiscreteMM1(lambda, 1.0);

List timeTaken = s.simulate(100000 / 2);
timeTakenBySystemNano[i] = (long) timeTaken.get(0);
timeTakenByStopWatch[i] = (long) timeTaken.get(1);
}

for (int i = 0; i < 500; i++) {
systemMean += timeTakenBySystemNano[i];
stopWatchMean += timeTakenByStopWatch[i];
}

long systemMeanAvg = systemMean / 500;
long stopWatchMeanAvg = stopWatchMean / 500;

System.out.println("Mean of system=" + systemMeanAvg);
System.out.println("Mean of stop=" + stopWatchMeanAvg);

long[] systemDeviations = new long[500];
long[] stopWatchDeviations = new long[500];
for (int i = 0; i < 500; i++) {
systemDeviations[i] = timeTakenBySystemNano[i] - systemMeanAvg;
stopWatchDeviations[i] = timeTakenByStopWatch[i] - stopWatchMeanAvg;
}

long systemDeviationSquareSum = 0;
long stopWatchDeviationSquareSum = 0;

// getting the squares of deviations
for (int i = 0; i < 500; i++) {
systemDeviationSquareSum += (systemDeviations[i] * systemDeviations[i]);

stopWatchDeviationSquareSum += (stopWatchDeviations[i] * stopWatchDeviations[i]);
}

long systemVariance = (systemDeviationSquareSum / (500 - 1));
long stopWatchVariance = (stopWatchDeviationSquareSum / (500 - 1));

System.out.println("-----------------------");
System.out.println("systemVariance=" + systemVariance);
System.out.println("stopWatchVariance" + stopWatchVariance);
double systemStdDeviation = Math.sqrt(systemVariance);
double stopWatchStdDeviation = Math.sqrt(stopWatchVariance);

System.out.println(" System deviation=" + systemStdDeviation);
System.out.println(" Stop Watch deviation=" + stopWatchStdDeviation);

}
}

