# zoomapi-java

[![](https://jitpack.io/v/dbchar/zoomapi-java.svg)](https://jitpack.io/#dbchar/zoomapi-java)
[![](https://jitci.com/gh/dbchar/zoomapi-java/svg)](https://jitci.com/gh/dbchar/zoomapi-java)

Java wrapper around the [Zoom.us](http://zoom.us) REST API v2.

This work has referenced [crista/zoomapi](https://github.com/crista/zoomapi) and [Zoomus](https://github.com/actmd/zoomus).

## Compatibility

This library requires **JDK 11** or above.

## Install

### Gradle

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.dbchar:zoomapi-java:0.0.2'
	}


### Maven

Step 1. Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.dbchar</groupId>
	    <artifactId>zoomapi-java</artifactId>
	    <version>0.0.2</version>
	</dependency>

## Usage

See [zoombot-java](https://github.com/dbchar/zoombot-java).

## Available methods

### Chat

- sendMessage
- history
- search

### Contacts

- list
- listExternal

### Chat Channels

- list
- create
- update
- delete
- get
- join
- leave
- listMembers
- inviteMembers
- deleteMember

### Chat Messages

- list
- post
- update
- delete

### Users

- list
- create
- update
- delete
- get

### Meetings

- get
- create
- delete
- list
- update

### Reports

- getAccountReport
- getUserReport

### Webinars

- create
- update
- delete
- list
- get
- end
- register

## License

MIT License

Copyright (c) 2020 double-charburger (Wen-Chia Yang, Junxian Chen)
