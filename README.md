# The Treep language - reference implementation #

Treep is a general-purpose programming language that revolves around **Tree Processing**.
It draws heavy inspiration from the Lisp family of languages, originating from the seminal work of John McCarthy and
his students on the language LISP, for LISt Processing.

The key design decision for Treep is to be a "programmable programming language" from the start and down to the core.
Its aim is to incorporate modern (ish) techniques in parsing and compiling and offer an API to control and extend the
parser and the compiler themselves, and thus the language itself. Treep is therefore both a language and a
meta-language; in other words, it is homoiconic.

We believe that all high-level programming is a form of language design and implementation (textual or graphical).
We want to give developers better tools to implement languages: DSLs, command languages, configuration languages, etc.
and the tools supporting them: editors, IDEs, debuggers, analysis tools, etc.

These are the key concepts of Treep:
* A syntax to represent trees:
  * a concrete, text-based syntax based on indentation a-la Python but with optional use of parentheses for grouping,
    like Lisp
  * an abstract, object-based syntax tree that can be fully manipulated by user programs
* First-class environments
* Rich compiler API: the compiler is a service that can be invoked and it has hooks for user-defined transformations
  and optimizations
