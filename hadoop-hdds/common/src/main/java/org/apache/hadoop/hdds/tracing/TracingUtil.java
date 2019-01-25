/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.tracing;

import java.lang.reflect.Proxy;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracingUtil {

  public static void initTrancing(String serviceName) {
    Configuration config = Configuration.fromEnv(serviceName);
    JaegerTracer tracer = config.getTracerBuilder()
        .registerExtractor(StringCodec.FORMAT, new StringCodec())
        .registerInjector(StringCodec.FORMAT, new StringCodec())
        .build();
    GlobalTracer.register(tracer);
  }

  public static String exportCurrentSpan() {
    StringBuilder builder = new StringBuilder();
    GlobalTracer.get().inject(GlobalTracer.get().activeSpan().context(),
        StringCodec.FORMAT, builder);
    return builder.toString();
  }

  public static Scope initializeScope(String name, String encodedParent) {
    Tracer.SpanBuilder spanBuilder;
    Tracer tracer = GlobalTracer.get();
    SpanContext parentSpan = null;
    if (encodedParent != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(encodedParent);
      parentSpan = tracer.extract(StringCodec.FORMAT, builder);

    }

    if (parentSpan == null) {
      spanBuilder = tracer.buildSpan(name);
    } else {
      spanBuilder =
          tracer.buildSpan(name).asChildOf(parentSpan);
    }
    return spanBuilder.startActive(true);
  }

  public static <T> T createProxy(T delegate, Class<T> interfce) {
    Class<?> aClass = delegate.getClass();
    return (T) Proxy.newProxyInstance(aClass.getClassLoader(),
        new Class<?>[] {interfce},
        new TraceAllMethod<T>(delegate, interfce.getSimpleName()));
  }

}
