# Backup Tool

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
   - [Merge Backup](#merge-backup)
   - [Full Backup](#full-backup)
   - [App Reinstaller](#app-reinstaller)
   - [Cloud Synchronization](#cloud-synchronization)
3. [Conclusion](#conclusion)
4. [License](#license)
5. [Installation](#installation)

---

## Introduction  

This project started as a trainee software project. After further testing and bug fixes, it will be good enough for practical use.  
I haven't done any alpha tests because I developed everything on my own.  
You can download it now or wait until I upload an installer for alpha testing.  
Before that, I need to collect more logs and improve exception handling.  
The alpha test installer may be available in a few weeks.
Take a look at the screenshots to become an overview of all features.
---

## Features
![img_4.png](img_4.png)
![img_3.png](img_3.png)
![img_5.png](img_5.png)

### Merge Backup
Creates new files and directories.
Overrides changes using last modified Date.
Doesn't copy any unchanged files. 

### Full Backup
Feature is not finished.
Not started.
Creates a new Instance of the selected source directory into target directory.

### App Reinstaller
Feature is not finished.
Reinstall all Apps.
Reloads all remembered Apps from web. 

### Cloud Synchronization
Not finished. Not started.

---

### AES Encryption
The content of a File is Encrypted. Not the Metadata.

### Validation
CRC32 Checksum from each file is ready.
SHA256 is not finished now.

---

## Conclusion
Only the Merge Backup feature is ready for testing.
An installer will be provided so you can test it without pulling.

---

## Test
test

---

## License

I copied the license from:  

https://choosealicense.com/community/ 

[license.txt](license.txt)

---

## Installation  

Download and run it in an IDE (IntelliJ).  
Dependencies are managed with Maven.  
I will create an installer for testing **merge backups, settings, restore, encryption, and validation** in a few weeks.  



