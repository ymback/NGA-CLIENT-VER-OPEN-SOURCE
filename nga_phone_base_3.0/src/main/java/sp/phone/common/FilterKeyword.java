package sp.phone.common;

public class FilterKeyword {
  private String keyword;
  private boolean enabled;

  public FilterKeyword() {}

  public FilterKeyword(String keyword) {
    this.keyword = keyword;
    this.enabled = true;
  }

  public String getKeyword() { return keyword; }
  public void setKeyword(String keyword) { this.keyword = keyword; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
}