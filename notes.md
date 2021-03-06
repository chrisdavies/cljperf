# Notes

Look through the change history to see what was being tested for each item noted below. The biggest perf-killer so far is logging. Pushing logging into a core.async channel seems like a reasonable solution.


## Running the tests

Run in production mode:

```
lein do clean, ring uberjar
java -jar target/cljperf-0.1.0-SNAPSHOT-standalone.jar

```

Then in a separate tab, run wrk 3 times to allow for warmup.

```
wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello

```

## Ring w/ plain text

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     3.79ms    7.84ms 125.13ms   97.11%
      Req/Sec   370.14     91.55     0.91k    77.15%
    737938 requests in 20.04s, 84.45MB read
  Requests/sec:  36827.07
  Transfer/sec:      4.21MB


## Routing (cljs-router)

Routing cost 2,218 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     4.69ms    9.91ms 128.78ms   95.36%
      Req/Sec   348.79    114.57     5.92k    78.32%
    695717 requests in 20.10s, 79.62MB read
  Requests/sec:  34609.85
  Transfer/sec:      3.96MB

## Routing (reitit)

Routing w/ reitit cost 1,139 requests/sec. This is the winning router
by a long shot (perf + features).

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     4.84ms   11.62ms 127.60ms   96.03%
      Req/Sec   358.82    104.35     0.95k    77.23%
    715621 requests in 20.05s, 81.90MB read
  Requests/sec:  35688.62
  Transfer/sec:      4.08MB

## Routing (bidi)

Bidi cost 6,515.07 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.41ms   11.97ms 159.60ms   95.90%
      Req/Sec   305.37    100.62     0.94k    76.21%
    607842 requests in 20.05s, 69.56MB read
  Requests/sec:  30312.57
  Transfer/sec:      3.47MB


## JSON (Cheshire)

JSON serialization cost 2,536 requests/sec... For a map w/ a single key.

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.31ms   11.70ms 230.90ms   95.32%
      Req/Sec   322.53    104.42     0.92k    77.14%
    643243 requests in 20.06s, 96.31MB read
  Requests/sec:  32073.39
  Transfer/sec:      4.80MB


## JSON camelCased w/ camel-snake-kebab.core

camelCase cost 2,618.39 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.89ms   11.94ms 128.42ms   94.75%
      Req/Sec   296.29    118.74   828.00     69.96%
    591089 requests in 20.07s, 88.50MB read
  Requests/sec:  29455.94
  Transfer/sec:      4.41MB


## JSON camelCased w/ simple regex

Simple regex cost us 209.39 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     4.38ms    8.63ms 128.40ms   96.67%
      Req/Sec   320.49     84.20     0.99k    80.50%
    638947 requests in 20.05s, 95.67MB read
  Requests/sec:  31864.45
  Transfer/sec:      4.77MB


## JSON w/ Jsonista + camelCase logic

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.14ms   11.16ms 156.13ms   95.83%
      Req/Sec   314.00    103.42     2.15k    78.39%
    628713 requests in 20.10s, 94.14MB read
  Requests/sec:  31273.05
  Transfer/sec:      4.68MB


## JSON w/ Jsonista w/out camelCase logic

Slight improvement (around 300 req/sec better than w/out camelCase)

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     4.60ms    8.87ms 120.29ms   96.12%
      Req/Sec   318.13    102.66     5.82k    77.51%
    634637 requests in 20.10s, 95.02MB read
  Requests/sec:  31573.26
  Transfer/sec:      4.73MB


## JSON w/ bigger payload

  {:msg-val "World!"
         :age 32
         :num-likes ["one" "two" "three"]
         :address {:street "Here we go"
                   :zip 3243}}

### Jsonista w/ camelCase

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.89ms   11.87ms 173.37ms   95.01%
      Req/Sec   283.60    106.31     1.17k    72.16%
    565345 requests in 20.06s, 132.63MB read
  Requests/sec:  28179.14
  Transfer/sec:      6.61MB

### Jsonista w/out camelCase

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.03ms   10.84ms 129.09ms   95.93%
      Req/Sec   318.07     92.36     1.58k    79.13%
    635931 requests in 20.11s, 150.40MB read
  Requests/sec:  31629.69
  Transfer/sec:      7.48MB

### Cheshire w/ camelCase

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.71ms   12.11ms 208.58ms   95.42%
      Req/Sec   294.20    107.03     1.11k    74.25%
    586485 requests in 20.07s, 137.59MB read
  Requests/sec:  29224.06
  Transfer/sec:      6.86MB

### Cheshire w/out camelCase

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.43ms   11.48ms 132.25ms   95.48%
      Req/Sec   302.62     99.14     1.30k    75.15%
    602793 requests in 20.05s, 142.57MB read
  Requests/sec:  30059.72
  Transfer/sec:      7.11MB

### Edn w/ pr-str

Surprisingly, edn is about 1,598 ops/sec slower than Cheshire + camelCase

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.69ms   11.11ms 171.76ms   95.16%
      Req/Sec   278.19     96.16     0.85k    74.11%
    554230 requests in 20.06s, 129.50MB read
  Requests/sec:  27626.06
  Transfer/sec:      6.45MB


## Logging each request w/ taoensso.timbre

Logging requests cost us 22,113 requests/sec!!! That's almost a 75% reduction.

  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency    10.38ms    5.43ms  79.26ms   74.92%
      Req/Sec    98.11     18.32   540.00     75.62%
    196045 requests in 20.10s, 29.35MB read
  Requests/sec:   9751.57
  Transfer/sec:      1.46MB


## Logging w/ middleware

Middleware cost us 600 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency    11.06ms    5.96ms  69.18ms   74.67%
      Req/Sec    92.03     18.83   710.00     75.25%
    183924 requests in 20.10s, 27.54MB read
  Requests/sec:   9151.44
  Transfer/sec:      1.37MB


## Logging w/ println

Logging w/ println cost us 20,908, about 1200 better than timbre

  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     9.24ms    4.40ms  70.34ms   69.98%
      Req/Sec   110.19     19.26   277.00     60.02%
    220301 requests in 20.11s, 32.98MB read
  Requests/sec:  10956.87
  Transfer/sec:      1.64MB


## Logging w/ println + core.async

Significantly faster than any previous logging option, cost us 5,301 requests/sec

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     6.46ms   13.03ms 170.46ms   94.72%
      Req/Sec   267.32    100.74     0.92k    74.97%
    533062 requests in 20.07s, 79.81MB read
  Requests/sec:  26563.27
  Transfer/sec:      3.98MB


## Logging w/ timbre + rate-limiting

Still pretty slow...

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency    11.02ms    5.90ms  68.78ms   74.96%
      Req/Sec    92.39     17.87   404.00     75.32%
    184714 requests in 20.10s, 27.66MB read
  Requests/sec:   9188.79
  Transfer/sec:      1.38MB

## Logging w/ timbre -> core.async -> println

Cost us 10,532 requests/sec vs not logging, about 1/2 as fast as async println

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     9.58ms   18.35ms 378.45ms   92.29%
      Req/Sec   214.46    103.80     1.52k    73.17%
    428739 requests in 20.10s, 64.19MB read
  Requests/sec:  21332.65
  Transfer/sec:      3.19MB

## Logging w/ core.async -> timbre -> println

Cost us 754 requests/sec vs not logging... not bad!

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3000/api/hello
  Running 20s test @ http://localhost:3000/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     5.49ms   11.69ms 233.77ms   94.92%
      Req/Sec   313.01    103.70     1.38k    77.31%
    623958 requests in 20.06s, 93.42MB read
  Requests/sec:  31110.26
  Transfer/sec:      4.66MB
