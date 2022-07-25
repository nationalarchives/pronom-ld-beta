package com.wallscope.pronombackend.model;

import java.util.ArrayList;
import java.util.List;

public class MarkdownHelpers {
    public static class FAQCategory {
        private final String title;
        private final ArrayList<FAQItem> items;

        public FAQCategory(String title, List<FAQItem> items) {
            this.title = title;
            this.items = new ArrayList<FAQItem>(items);
        }

        public FAQCategory(String title) {
            this.title = title;
            this.items = new ArrayList<>();
        }


        public String getTitle() {
            return title;
        }

        public List<FAQItem> getItems() {
            return items;
        }

        public void addItem(FAQItem i) {
            items.add(i);
        }

        @Override
        public String toString() {
            return "FAQCategory{" +
                    "title='" + title + '\'' +
                    ", items=" + items +
                    '}';
        }

        public static class FAQItem {
            private final String title;
            private final String text;

            public FAQItem(String title, String text) {
                this.title = title;
                this.text = text;
            }

            public String getTitle() {
                return title;
            }

            public String getText() {
                return text;
            }

            @Override
            public String toString() {
                return "FAQItem{" +
                        "title='" + title + '\'' +
                        ", text='" + text + '\'' +
                        '}';
            }
        }
    }

    public static class NumberedStep {
        private final String title;
        private final String text;
        private final String example;

        public NumberedStep(String title, String text, String example) {
            this.title = title;
            this.text = text;
            this.example = example;
        }

        public String getText() {
            return text;
        }

        public String getExample() {
            return example;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "NumberedStep{" +
                    "title='" + title + '\'' +
                    ", text='" + text + '\'' +
                    ", example='" + example + '\'' +
                    '}';
        }
    }
}
