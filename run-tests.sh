#!/usr/bin/env bash
java  -cp .:libs/junit.jar:libs/org.hamcrest.core_1.3.0.v201303031735.jar:out/production/stream-sampler \
 org.junit.runner.JUnitCore  org.digfish.tests.StreamSamplerTestCase
