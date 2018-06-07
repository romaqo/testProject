package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsoupFindById {

    private static Logger LOGGER = LoggerFactory.getLogger(JsoupFindById.class);

    private static String CHARSET_NAME = "utf8";

    private static final String TARGET_ELEMENT_ID = "make-everything-ok-button";
    private static String[] targetElementClasses = {"btn-success", "test-link-ok"};

    public static void main(String[] args) {

        Stream<String> pages = Stream.of("./pages/sample-0-origin.html", "./pages/sample-1-evil-gemini.html",
                "./pages/sample-2-container-and-clone.html", "./pages/sample-3-the-escape.html", "./pages/sample-4-the-mash.html");

        pages.forEach(page -> {
            LOGGER.info(page + ": ");
            runAlgorithm(page);
        });
    }

    private static void runAlgorithm(String fileName) {
        Optional<Element> targetElemnt = findElement(new File(fileName), TARGET_ELEMENT_ID, targetElementClasses);
        Elements parents = findParents(targetElemnt.get());
        printPath(parents, targetElemnt.get());
    }

    private static Optional<Element> findElement(File htmlFile, String targetElementId, String[] targetElementClasses) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            Optional<Element> element = findElementById(doc, targetElementId);
            if (!element.isPresent()) {
                element = findElementByClass(doc, targetElementClasses);
            }
            return element;


        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<Element> findElementById(Document document, String targetElementId) {
        return Optional.ofNullable(document.getElementById(targetElementId));
    }

    private static Optional<Element> findElementByClass(Document document, String[] targetElementClasses) {
        Optional<Element> element = Optional.ofNullable(document.getElementsByClass(targetElementClasses[0]).first());
        if (!element.isPresent()) {
            element = Optional.ofNullable(document.getElementsByClass(targetElementClasses[1]).first());
        }
        return element;

    }

    private static Elements findParents(Element element) {
        return element.parents();
    }

    private static void printPath(Elements elements, Element element) {

        Collections.reverse(elements);
        elements.add(element);
        String pathToElement = elements.stream()
                .map(e -> String.format("%s[%s]", e.tagName(), e.elementSiblingIndex()))
                .collect(Collectors.joining(" > "));
        LOGGER.info("Path to target: [{}]", pathToElement);
    }

}
