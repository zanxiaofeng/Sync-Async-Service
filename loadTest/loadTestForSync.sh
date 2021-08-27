#!/bin/env bash

ab -n 200000 -kc 2000 -s 1200 http://localhost:8080/Sync-Async-Service/SyncLongRunningServlet\?number\=1000000\&sleeps\=5