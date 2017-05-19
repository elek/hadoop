# Apache Hadoop site

## Usage

The site is generated with [Hugo](https://gohugo.io/). 

To generate a site you need only one binary. Follow the install guide from the hugo site. (Typically all the package managers 
contain dedicated package, but as the hugo is written in go it's also possible to download the single binary from the release page).

After the installation you can generate the site with:

`hugo`

And the site will be generated to the `public` sub directory.

To improve/develop the site, you can use

```
hugo server --refresh
```

which starts a standalone auto-refreshed web server.

## Content

To create a new relase, create a file in  ```content/release``` directory. The file name should be ```<version>.md```where version is the release version.

Example: content/release/2.7.3.md

```
---
title: Release 2.7.3 available
date: 2016-08-26
linked: true
---

Please see the [Hadoop 2.7.3 Release
Notes](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-common/releasenotes.html)
for the list of 221 bug fixes and patches since the previous release
2.7.2.
```

`linked: true` attributes means, that it will be displayed on the release page and under the documentation menu.

Note: date is used to sort the releases when the latests are displayed in the site.
