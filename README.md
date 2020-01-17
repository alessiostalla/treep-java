# The Treep language - reference implementation #

Treep is a general-purpose programming language that revolves around **Tree Processing**. It draws heavy inspiration from the Lisp family of languages, originating from the seminal work of John McCarthy and his students on the language LISP, for LISt Processing.

The key design decision for Treep is to be a "programmable programming language" from the start and down to the core. Its aim is to incorporate modern (ish) techniques in parsing and compiling and offer an API to control and extend the parser and the compiler themselves, and thus the language itself. Treep is therefore both a language and a meta-language (in other words, it is homoiconic).

We believe that all high-level programming is language design and implementation. We want to give developers better tools to implement languages: DSLs, command languages, configuration languages, etc. and their surrounding tools: editors, IDEs, debuggers, analysis tools, etc.