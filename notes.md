Perf

```
lein do clean, ring uberjar
java -jar target/cljperf-0.1.0-SNAPSHOT-standalone.jar
wrk -c 100 -d 20 -t 100 http://localhost:3001/api/hello
```

For each test, run wrk 3 times to allow for warmup.

## Ring w/ plain text

  ~/dev/lang/clojure/cljperf (master)*$ wrk -c 100 -d 20 -t 100 http://localhost:3001/api/hello
  Running 20s test @ http://localhost:3001/api/hello
    100 threads and 100 connections
    Thread Stats   Avg      Stdev     Max   +/- Stdev
      Latency     3.79ms    7.84ms 125.13ms   97.11%
      Req/Sec   370.14     91.55     0.91k    77.15%
    737938 requests in 20.04s, 84.45MB read
  Requests/sec:  36827.07
  Transfer/sec:      4.21MB


## Routing



## JSON
## Logging
## Logging w/ middleware

