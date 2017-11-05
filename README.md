# cljperf

A throwaway project, used to test the performance impact of various libraries, techniques, etc.

## For details, see the git history

And have a look at [notes.md](./notes.md).

All tests were run on an Intel i5/8GB Macbook.

## Logging

Logging to stdout is super slow, as in it reduces requests/second by about 22,113 on my laptop. I remember seeing this same effect in C# years ago. Buffering the log is an effective workaround.

Timbre's rate limiting doesn't seem too terribly effective at improving things, though I didn't tweak it much. However, tossing log messages into a core.async channel, and then logging to Timbre from that channel *really* improves things.

The downside of this approach is that you increase your memory usage (the log messages are stored in a channel buffer for a while), and you increase your odds of missing messages.

It's a tradeoff that you'd want to evaluate on a case-by-case basis.

## License

Creative Commons, no attribution