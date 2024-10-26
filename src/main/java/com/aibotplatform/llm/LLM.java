package com.aibotplatform.llm;

import reactor.core.publisher.Flux;

public interface LLM {
    Flux<String> chat(String input);
}