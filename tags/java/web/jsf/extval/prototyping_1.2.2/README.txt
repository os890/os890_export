if you start prototyping an app, you might have input components without value-bindings.
input components without bindings don't make much sense in a real app.
this simple add-on allows you to skip validation for input-components without value-bindings.

just copy the package + classes to your webapp -> it's done!
>or<
if you don't like to introduce this package, just take the impl. and register your StartupListener as normal jsf phase-listener.