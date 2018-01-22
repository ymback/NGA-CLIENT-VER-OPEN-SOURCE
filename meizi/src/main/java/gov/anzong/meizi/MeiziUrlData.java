package gov.anzong.meizi;

/**
 * Every single topic(or a single pic in the main page) should be regarded as a
 * MeiziM. Based on the field doubanPosterurl, different MeiziM could be of the
 * same DoubanPoster. But right now, I haven't taken care of this yet.
 *
 * @author Norman
 */
public class MeiziUrlData {

    public String smallPicUrl;

    public String largePicUrl;

    public int starCount;

    /**
     * If this MeiziM(pic) is stared by the current user, false if not logged in
     */
    public boolean stared;

    /**
     * The poster of this MeiziM(pic) in douban.com
     */
    public String doubanPosterUrl;

    /**
     * Not used yet, but seems to be a data id of this MeiziM in the server
     */
    public String dataId;

    /**
     * The url for the original topic in douban.com, this field need to be
     * retrieved from the topic page.
     */
    public String TopicUrl;
}
