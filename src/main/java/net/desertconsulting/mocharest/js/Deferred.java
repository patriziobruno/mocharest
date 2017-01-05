/* 
 * Copyright 2017 Patrizio Bruno <desertconsulting@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.desertconsulting.mocharest.js;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;

/**
 * A
 * <a href="https://api.jquery.com/category/deferred-object/">JQuery.deferred</a>
 * inspired class for deferred operations.
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class Deferred {

    private boolean resolved = false;
    private Object resolvedResult;

    private boolean rejected = false;
    private Object rejectedResult;

    private JSObject doneHandler;
    private JSObject failHandler;

    private Deferred parent;
    private List<Deferred> children;

    public Deferred() {
        children = new ArrayList<>();
    }

    private Deferred(Deferred parent) {
        this();
        this.parent = parent;
    }

    public void resolve(Object result) {
        if (!resolved && !rejected) {
            resolved = true;
            resolvedResult = result;

            rejected = false;
            rejectedResult = null;

            runHandlers(null);
        }
    }

    public void reject(Object result) {
        if (!resolved && !rejected) {

            rejected = true;
            rejectedResult = result;

            resolved = false;
            resolvedResult = null;

            runHandlers(null);
        }
    }

    public Deferred done(JSObject handler) {
        if (handler.isFunction()) {
            doneHandler = handler;

            Deferred rv = new Deferred(this);
            children.add(rv);
            if (resolved) {
                rv.resolve(resolvedResult);
            }
            return rv;
        }
        throw new IllegalArgumentException("handler");
    }

    public Deferred fail(JSObject handler) {

        if (handler.isFunction()) {
            failHandler = handler;

            Deferred rv = new Deferred(this);
            children.add(rv);

            if (rejected) {
                rv.reject(rejectedResult);
            }
            return rv;
        }
        throw new IllegalArgumentException("handler");
    }

    public Deferred always(JSObject handler) {

        if (handler.isFunction()) {
            doneHandler = handler;
            failHandler = handler;

            Deferred rv = new Deferred(this);
            children.add(rv);
            if (resolved) {
                rv.resolve(resolvedResult);
            } else if (rejected) {
                rv.reject(rejectedResult);
            }
            return rv;
        }
        throw new IllegalArgumentException("handler");
    }

    private void runHandlers(Object res) {

        Object result = null;
        JSObject handler = null;

        if (resolved) {
            result = res != null ? res : resolvedResult;
            handler = doneHandler;
        } else if (rejected) {
            result = res != null ? res : rejectedResult;
            handler = failHandler;
        }

        Function<Object, Consumer<Deferred>> consumerGenerator = (r) -> {
            Consumer<Deferred> consumer = null;

            if (resolved) {
                consumer = (p) -> {
                    p.resolve(r);
                };
            } else if (rejected) {
                consumer = (p) -> {
                    p.reject(r);
                };
            }
            return consumer;
        };

        if (handler != null) {
            Object rv;
            try {
                rv = handler.call(this, result);
                if (res == null) {
                    Object parm;

                    if (rv != null && !(rv instanceof Undefined)) {
                        parm = rv;
                    } else {
                        parm = result;
                    }
//
//                if (parent != null) {
//                    if (resolved) {
//                        parent.resolve(result);
//                    } else if (rejected) {
//                        parent.reject(res);
//                    }
//                }

                    if (parm instanceof Deferred) {
                        Deferred childPromise = (Deferred) parm;
                        if (childPromise.children.isEmpty() && !children.
                                isEmpty()) {
                            childPromise.children.addAll(children);
                        } else if (!childPromise.children.isEmpty()) {
                            childPromise.children.get(childPromise.children.
                                    size() - 1).children.addAll(children);
                        }
                        childPromise.runHandlers(null);
                    } else {
                        Consumer<Deferred> childrenConsumer = consumerGenerator.apply(parm);
                        if (childrenConsumer != null) {
                            children.stream().forEach(childrenConsumer);
                        }
                    }
                }
            } catch (Exception ex) {
                resolved = false;
                rejected = true;

                runHandlers(ex);
            }
        } else if (resolved || rejected) {
            Consumer<Deferred> childrenConsumer = consumerGenerator.apply(result);
            if (childrenConsumer != null) {
                children.stream().forEach(childrenConsumer);
            }
        }
    }
}
