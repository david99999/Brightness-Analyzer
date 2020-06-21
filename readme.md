# NATIVE REAL TIME BRIGHTNESS ANALYZER

This repository contains an Android Application that analyzes the frames of the camera via a native C++ library and shows on a custom view (HistogramView) the representation of the dark and bright pixels.

<p align="center">
  <a href="https://youtu.be/MbfNtqKKbHI" target="_blank"><img height="400" src="https://raw.githubusercontent.com/david99999/Brightness-Analyzer/master/artwork/demo.webp"></a>
</p>

# Tech used

A few tools were used to create the App and Native Library and allow its usage as a gradle dependency.

## C++ Native analyzer
Contains a C++ analyzer that iterates through the frame's pixels and calculate basic average brightness

## Custom View for Histogram representation
A View to show a graphic bars indicating the bright and dark pixels on the image

## Android Jetpack CameraX
Showcases the simplicity of jetpack to setup a camera preview and a real time analyzer

## Android Data Binding Library
Showcases how easy is to bind layouts and UI Logic

## Github Packages
The native library contains a Kotlin wrapper library, this repository shows how to publish to Github Packages and consume it as a gradle dependency

## Kotlin 1.3.72 for Library and Demo App
Both library and Demo App are written in Kotlin v1.3.72