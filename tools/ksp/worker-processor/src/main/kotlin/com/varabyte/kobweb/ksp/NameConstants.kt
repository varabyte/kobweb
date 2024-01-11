package com.varabyte.kobweb.ksp

private const val KOBWEB_FQN_PREFIX = "com.varabyte.kobweb."
private const val KOBWEB_WORKER_FQN_PREFIX = "${KOBWEB_FQN_PREFIX}worker."

const val WORKER_FQN = "${KOBWEB_WORKER_FQN_PREFIX}Worker"
const val WORKER_STRATEGY_SIMPLE_NAME = "WorkerStrategy"
const val WORKER_STRATEGY_FQN = "${KOBWEB_WORKER_FQN_PREFIX}$WORKER_STRATEGY_SIMPLE_NAME"
