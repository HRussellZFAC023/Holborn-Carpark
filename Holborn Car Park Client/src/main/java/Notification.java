import java.util.Date;

public class Notification {
    private String _id;
    private String title;
    private String description;
    private Date created;

    /**
     * Empty constructor
     */
    public Notification() {
        _id = "";
        title = "";
        description = "";
        created = null;
    }

    /**
     * @param _id
     * @param title
     * @param description
     */
    public Notification(String _id, String title, String description, Date created) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.created = created;
    }
    public Notification( String title, String description) {
        this._id = null;
        this.title = title;
        this.description = description;
        this.created = null;
    }
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "_id='" + _id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
