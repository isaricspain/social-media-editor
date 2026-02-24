package dev.langchain4j.model.chat;

/**
 * Minimal ChatLanguageModel interface compatible with LangChain4j API surface.
 * This allows wiring application code to the abstraction without external deps.
 */
public interface ChatLanguageModel {
    String generate(String prompt);
}
