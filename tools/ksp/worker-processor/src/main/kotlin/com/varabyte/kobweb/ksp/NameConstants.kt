package com.varabyte.kobweb.ksp

private const val KOBWEB_FQN_PREFIX = "com.varabyte.kobweb."
private const val KOBWEB_WORKER_FQN_PREFIX = "${KOBWEB_FQN_PREFIX}worker."

const val WORKER_FQN = "${KOBWEB_WORKER_FQN_PREFIX}Worker"
const val WORKER_FACTORY_SIMPLE_NAME = "WorkerFactory"
const val WORKER_FACTORY_FQN = "${KOBWEB_WORKER_FQN_PREFIX}$WORKER_FACTORY_SIMPLE_NAME"
