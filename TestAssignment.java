import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import com.opencsv.CSVWriter;

public class TestAssignment {

    public static void main(String args[]) {
        String wikipediaLink = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        int n = 3; // Number of cycles

        try {
            // Step 1: Validate the Wikipedia link
            if (!isValidWikiLink(wikipediaLink)) {
                throw new IllegalArgumentException("Invalid Wikipedia link format.");
            }

            // Step 2: Start scraping process
            Set<String> visitedLinks = new LinkedHashSet<>();
            Set<String> linksToVisit = new LinkedHashSet<>();
            linksToVisit.add(wikipediaLink);

            for (int cycle = 0; cycle < n; cycle++) {
                Set<String> newLinks = new LinkedHashSet<>();
                for (String link : linksToVisit) {
                    if (!visitedLinks.contains(link)) {
                        // Step 3: Scrape the Wikipedia page for links
                        newLinks.addAll(scrapeWikiPage(link));
                        visitedLinks.add(link);
                    }
                    if (visitedLinks.size() >= 10) {
                        break; // Limit to 10 unique links
                    }
                }
                linksToVisit.addAll(newLinks);
            }

            // Step 4: Output the collected unique links
            System.out.println("Adding the visited links");

            writeLinksToCSV(visitedLinks);

            System.out.println("Links has been added in the CSV file successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidWikiLink(String link) {
        return link.startsWith("https://en.wikipedia.org/wiki/");
    }

    private static Set<String> scrapeWikiPage(String url) throws IOException {
        Set<String> links = new LinkedHashSet<>();
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a[href^=\"/wiki/\"]");

        for (Element element : elements) {
            String absUrl = element.absUrl("href");
            if (absUrl.startsWith("https://en.wikipedia.org/wiki/") && !absUrl.equals(url)) {
                links.add(absUrl);
            }
            if (links.size() >= 10) {
                break; // Limit to 10 unique links per page
            }
        }

        return links;
    }

    private static void writeLinksToCSV(Set<String> links) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter("links.csv"))) {
            String[] header = {"Link"};
            writer.writeNext(header);
            for (String link : links) {
                writer.writeNext(new String[]{link});
            }
        }
    }
    }

