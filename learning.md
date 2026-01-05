## Hour 1 — API Contract & Agent Flow Design

- Designed a structured API contract for incident analysis
- Defined multi-stage agent workflow for secure AI reasoning
- Learned the importance of locking JSON schemas early
- Focused on explainability and safety-first design

## Hour 2 — Incident Classification Engine

- Implemented real-time incident classification using LLM (OpenRouter/OpenAI)
- Designed a strict system prompt to force structured JSON output
- Implemented safety-first logic: confidence threshold (0.6) to prevent speculation
- Learned how to handle LLM response parsing and category mapping in Spring Boot

## Hour 3 — Policy Dataset & Policy Loader

- Structured a trusted security policy knowledge base in the resources folder.
- Implemented a deterministic mapping between incident types and specific policy files.
- Built a `PolicyLoaderService` to reliably load policy text from the classpath.
- Prepared the foundation for Retrieval-Augmented Generation (RAG) by ensuring the agent only uses approved data.


## Hour 4 — Embeddings & Vector Search (RAG Core)

- Implemented semantic retrieval using text embeddings (`text-embedding-3-small`).
- Built an in-memory `VectorStoreService` to store and manage security policy embeddings.
- Implemented **Cosine Similarity** from scratch to find the best semantic match for incidents.
- Learned the power of RAG: "suspicious behavior" now correctly maps to "unauthorized access" regardless of literal keywords.
