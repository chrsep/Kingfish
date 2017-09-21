# Kingfish

*I will (at least I expect to) be graduating on 2018.
From then in I won't be maintaining Portal anymore since I will no longer be studying on Binus.
Let us know If you're interested in keeping Portal alive, well after 2018.*

---

[![CircleCI](https://circleci.com/gh/chrsep/Kingfish.svg?style=svg)](https://circleci.com/gh/chrsep/Kingfish)
[![codecov](https://codecov.io/gh/chrsep/Kingfish/branch/master/graph/badge.svg)](https://codecov.io/gh/chrsep/Kingfish)

Portal gives you instant and easy access to your schedules and other
university-related data that are available on Binusmaya, online or offline.

This code uses the libraries and language listed below extensively.
Basic knowledge on the functionality and design of these libraries and
language will be required to understand this codebase.

#### [Kotlin (Programming language)](https://kotlinlang.org/)
A programming language developed by Jetbrains, the team behind Intellij
and Android Studio. It reduces boilerplate significantly and a lot more `fun` to use than java. To learn or try kotlin, you can use the the
[Kotlin Koans](http://try.kotlinlang.org/)

#### [Realm (Database)](https://realm.io/)
An object database built from the ground up for mobile application

#### Libraries
1. [ReactiveX](http://reactivex.io/), used to handle asynchronous task
2. [Anko](https://github.com/Kotlin/anko), a library to interact with layouts, based on kotlin
3. [Retrofit](http://square.github.io/retrofit/), for handling network calls
4. [Joda-Time](http://www.joda.org/joda-time/) for handling date and time

#### Other DevTools
1. [Stetho](http://facebook.github.io/stetho/), basically a chrome devtool for android by facebook, insanely awesome and useful

#### Architecture
This is my first time implementing software design pattern and architecture for real, so it's still really rough and messy. Feedback will be very appreciated.

1. [Dagger 2](https://google.github.io/dagger/), Dependency Injection (DI)
2. Portal tries to mimic the [MVP+Clean architecture](https://medium.com/@dmilicic/a-detailed-guide-on-developing-android-apps-using-the-clean-architecture-pattern-d38d71e94029), below are the type of classes Portal currently have
    1. **Presenter**, binds any interaction with the view (show snackbars, clicks, etc) to the interactors
    2. **Interactor**, contains all of the business logics (Filter journal items, calculate semester)
    3. **Repositories**, provides and manages data (save and retrieve cookies from sharedpreferencees, save and retrieve schedules from realm)
    4. **Networks**, handles and creates all the network calls to Binusmaya APIs
    5. **Models**, simple objects that will be used to store data.
    

*To build Portal, Android Studio 3.0+ with Kotlin plugin is required. This consider this as an experimental project to try new technologies, so I tend to go with experimental stuff here (Eg. dependencies and tools that's still on beta or
alpha stage).*

### Contributing

This codebase is open, **If you want to learn about android**, contributing to fix bugs and defects, or even implements new functionality (like attendance info for example) in Portal is one of the best way to learn quickly. I always believe that working in real projects is the only way to truly learn how to do something.

I wanted Portal to be a community project where anyone can contribute because i don't believe i can fix all the problems it have alone, since I'm still not an expert and I don't know or have full access to Binusmaya's infrastructure. Besides, great  softwares aren't build by one guy 😎.

In the end, we are here to learn to better work and collaborate with multiple people and build better software. I'd love if anyone wanted to contribute to this project 😁.

#### How To contribute

This is my first time dealing with open source contribution, but here are my suggestion on how to start:

1. Create a new issue, introduce your suggestion or what you wanted to do so that we can have a discussion.
2. Fork the repository and push your changes into your repository
3. Create a pull request to the develop branch
4. We'll review the code changes and discuss any possible improvement together
5. When the code is ready it'll be merged to develop.

I'm trying to follow [GitFlow's](https://datasift.github.io/gitflow/IntroducingGitFlow.html) branching model.
I'm still figuring out how to do this so this is just a suggestion from me.

