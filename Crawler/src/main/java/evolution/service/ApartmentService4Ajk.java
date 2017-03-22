package evolution.service;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Service;

import evolution.entity.Apartment;
import evolution.util.Csv;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApartmentService4Ajk implements ApartmentService<Apartment> {
	@Override
	public List<Apartment> search(String url) {
		try {
			Elements zuItemModes = Jsoup.connect(url).get().select("div.zu-itemmod");
			List<Apartment> apartments = new LinkedList<>();
			for (Element zuItemMode : zuItemModes) {
				Map<String, String> info = new HashMap<>();
				String link = zuItemMode.attr("link");
				info.put("url", link);// Get the URL.
				try {
					Document document = Jsoup.connect(link).get();
					// Get the price.
					String price = document.select("span.f26").first().ownText();
					info.put("price", price);
					// Get the relevant information.
					Elements pPhrases = document.select("dl.p_phrase.cf");// Selects dl tags with class="p_phrase cf". 
					for (Element pPhrase : pPhrases) {
						String key = pPhrase.select(">dt").first().ownText();// >dt selects the child dt tags.
						String value = pPhrase.select(">dd").first().ownText();
						info.put(key, value);
					}
					// Get the phone number.
					info.put("phone", document.select("div.broker_tel").get(0).ownText().replaceAll(" ", ""));
				} catch (Exception e) {}
				apartments.add(tidy(info));
			}
			return apartments.stream()
					.map(x -> {
						x.setPricePerArea(x.getPrice() / x.getArea());
						return x;
					}).sorted(Comparator.comparing(Apartment::getPrice)
							.thenComparing(Apartment::getPricePerArea)
							.thenComparing(Apartment::getApartmentScore))
					.collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Apartment tidy(Map<String, String> info) {
		log.info(info.toString());
		Apartment apartment = new Apartment();
		apartment.setPrice(new Double (info.get("price")));
		String area = info.get("面积");
		apartment.setArea(new Double(area.substring(0, area.length() - 2)));
		apartment.setUrl(info.get("url"));
		apartment.setPhone(info.get("phone"));
		apartment.setLeaseType(info.get("租赁方式"));
		apartment.setRentType(info.get("租金押付"));
		String apartmentType = info.get("房型");
		try {
			apartment.setApartmentScore(new Integer(apartmentType.substring(0, 1)) + new Integer(apartmentType.substring(2, 3)) + new Integer(apartmentType.substring(4, 5)));
		} catch(Exception e) {
			apartment.setApartmentScore(Integer.MAX_VALUE);
		}
		apartment.setApartmentType(apartmentType);
		apartment.setAddress(info.get("地址"));
		return apartment;
	}
	
	@Test
	public void test() throws Exception {
		String url = "http://sz.zu.anjuke.com/ditie/zj21/";
		List<Apartment> apartments = search(url);
		System.out.println(apartments);
		Csv.toFile(apartments, new File("D:/Data/Apartments.csv"));
	}
}	
