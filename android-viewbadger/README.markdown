Android ViewBadger
==================

A simple way to "badge" any given Android view at runtime without having to cater for it in layout.

![Demos](http://www.jeffgilfelt.com/viewbadger/vb-1a.png "Demos")&nbsp;
![ListAdapter](http://www.jeffgilfelt.com/viewbadger/vb-2a.png "ListAdapter")

Note: If your aim is to replicate the iOS icon and TabBar badge UI for notifications, consider using Android UI conventions such as the number field of the [Notification](http://developer.android.com/reference/android/app/Notification.html "Notification") class rather than this method.

Usage
-----

Simple example:

    View target = findViewById(R.id.target_view);
    BadgeView badge = new BadgeView(this, target);
    badge.setText("1");
    badge.show();

This project contains a fully working example application. Refer to the `DemoActivity` class for more custom badge examples, including custom backgrounds and animations. `BadgeView` is a subclass of `TextView` so you can use all of `TextView`'s methods to style the appearance of your badge.

To use ViewBadger in your own Android project, simply copy `android-viewbadger.jar` (available from this repository's package downloads) into your project's `/libs` directory and add it to the build path.

Current Limitations
-------------------

- Badging Action Bar items is currently not supported [#2](https://github.com/jgilfelt/android-viewbadger/issues/2)
- Badging views inside RelativeLayout with dependencies may break alignment [#1](https://github.com/jgilfelt/android-viewbadger/issues/1)

Credits
-------

Author: Jeff Gilfelt

The code in this project is licensed under the Apache Software License 2.0.
<br />
Copyright (c) 2011 readyState Software Ltd.
