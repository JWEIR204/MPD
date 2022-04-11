//Jack Weir S1830098
package org.me.gcu.weir_jack_s1830098;

public class RoadItem {

    private String title;
    private String description;
    private String link;
    private String geoPoint;
    private String pubDate;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getLink(){
        return link;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getGeoPoint(){
        return geoPoint;
    }

    public void setGeoPoint(String geoPoint){
        this.geoPoint = geoPoint;
    }

    public String getPubDate(){
        return pubDate;
    }

    public void setPubDate(String pubDate){
        this.pubDate = pubDate;
    }

    public RoadItem(String title, String description, String link, String geoPoint, String pubDate)
    {
        this.title = title;
        this.description = description;
        this.link = link;
        this.geoPoint = geoPoint;
        this.pubDate = pubDate;
    }

    public RoadItem()
    {
        title = "";
        description = "";
        link = "";
        geoPoint = "";
        pubDate = "";
    }

    public String toString(){
        String convert;

        convert = pubDate + "\n" + title + "\n" + description + "\n\n\n";
        return convert;
    }
}

