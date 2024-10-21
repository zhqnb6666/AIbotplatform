package com.aibotplatform.llm;

import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public interface Assistant {
    Result<String> chat(String userMessage);
}
