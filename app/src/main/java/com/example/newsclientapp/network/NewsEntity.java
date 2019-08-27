/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.network;

import java.io.Serializable;
import java.util.List;

class Keyword implements Serializable {
	private double score;
	private String word;

	public double getScore () {
		return score;
	}

	public void setScore (double score) {
		this.score = score;
	}

	public String getWord () {
		return word;
	}

	public void setWord (String word) {
		this.word = word;
	}
}

class MentionedTime implements Serializable {
	private double score;
	private String word;

	public double getScore () {
		return score;
	}

	public void setScore (double score) {
		this.score = score;
	}

	public String getWord () {
		return word;
	}

	public void setWord (String word) {
		this.word = word;
	}
}

class Person implements Serializable {
	private int count;
	private String linkedURL;
	private String mention;

	public int getCount () {
		return count;
	}

	public void setCount (int count) {
		this.count = count;
	}

	public String getLinkedURL () {
		return linkedURL;
	}

	public void setLinkedURL (String linkedURL) {
		this.linkedURL = linkedURL;
	}

	public String getMention () {
		return mention;
	}

	public void setMention (String mention) {
		this.mention = mention;
	}
}

class Organization implements Serializable {
	private int count;
	private String linkedURL;
	private String mention;

	public int getCount () {
		return count;
	}

	public void setCount (int count) {
		this.count = count;
	}

	public String getLinkedURL () {
		return linkedURL;
	}

	public void setLinkedURL (String linkedURL) {
		this.linkedURL = linkedURL;
	}

	public String getMention () {
		return mention;
	}

	public void setMention (String mention) {
		this.mention = mention;
	}
}

class MentionedPlace implements Serializable {
	private double lng;
	private int count;
	private String linkedURL;
	private double lat;
	private String mention;

	public double getLng () {
		return lng;
	}

	public void setLng (double lng) {
		this.lng = lng;
	}

	public int getCount () {
		return count;
	}

	public void setCount (int count) {
		this.count = count;
	}

	public String getLinkedURL () {
		return linkedURL;
	}

	public void setLinkedURL (String linkedURL) {
		this.linkedURL = linkedURL;
	}

	public double getLat () {
		return lat;
	}

	public void setLat (double lat) {
		this.lat = lat;
	}

	public String getMention () {
		return mention;
	}

	public void setMention (String mention) {
		this.mention = mention;
	}
}

class NewsLocation implements Serializable {
	private double score;
	private String word;

	public double getScore () {
		return score;
	}

	public void setScore (double score) {
		this.score = score;
	}

	public String getWord () {
		return word;
	}

	public void setWord (String word) {
		this.word = word;
	}
}

class NewsFigure implements Serializable {
	private double score;
	private String word;

	public double getScore () {
		return score;
	}

	public void setScore (int score) {
		this.score = score;
	}

	public String getWord () {
		return word;
	}

	public void setWord (String word) {
		this.word = word;
	}
}

public class NewsEntity implements Serializable {

	private String image;
	private String publishTime;
	private List<Keyword> keywords;
	private String language;
	private String video;
	private String title;
	private List<MentionedTime> when;
	private String content;
	private List<Person> persons;
	private String newsID;
	private String crawlTime;
	private List<Organization> organizations;
	private String publisher;
	private List<MentionedPlace> locations;
	private List<NewsLocation> where;
	private String category;
	private List<NewsFigure> who;

	public String getImage () {
		return image;
	}

	public void setImage (String image) {
		this.image = image;
	}

	public String [] getImageURLs () {
		if (this.image.length() < 3)
			return null;
		return this.image.substring(1, this.image.length() - 1).split(":");
	}

	public String getPublishTime () {
		return publishTime;
	}

	public void setPublishTime (String publishTime) {
		this.publishTime = publishTime;
	}

	public List<Keyword> getKeywords () {
		return keywords;
	}

	public void setKeywords (List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public String getLanguage () {
		return language;
	}

	public void setLanguage (String language) {
		this.language = language;
	}

	public String getVideo () {
		return video;
	}

	public void setVideo (String video) {
		this.video = video;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public List<MentionedTime> getWhen () {
		return when;
	}

	public void setWhen (List<MentionedTime> when) {
		this.when = when;
	}

	public String getContent () {
		return content;
	}

	public void setContent (String content) {
		this.content = content;
	}

	public List<Person> getPersons () {
		return persons;
	}

	public void setPersons (List<Person> persons) {
		this.persons = persons;
	}

	public String getNewsID () {
		return newsID;
	}

	public void setNewsID (String newsID) {
		this.newsID = newsID;
	}

	public String getCrawlTime () {
		return crawlTime;
	}

	public void setCrawlTime (String crawlTime) {
		this.crawlTime = crawlTime;
	}

	public List<Organization> getOrganizations () {
		return organizations;
	}

	public void setOrganizations (List<Organization> organizations) {
		this.organizations = organizations;
	}

	public String getPublisher () {
		return publisher;
	}

	public void setPublisher (String publisher) {
		this.publisher = publisher;
	}

	public List<MentionedPlace> getLocations () {
		return locations;
	}

	public void setLocations (List<MentionedPlace> locations) {
		this.locations = locations;
	}

	public List<NewsLocation> getWhere () {
		return where;
	}

	public void setWhere (List<NewsLocation> where) {
		this.where = where;
	}

	public String getCategory () {
		return category;
	}

	public void setCategory (String category) {
		this.category = category;
	}

	public List<NewsFigure> getWho () {
		return who;
	}

	public void setWho (List<NewsFigure> who) {
		this.who = who;
	}
}
