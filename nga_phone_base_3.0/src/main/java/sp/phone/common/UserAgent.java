package sp.phone.common;

public class UserAgent {
  private String keyword;
  private boolean enabled;

  public UserAgent() {}

  public UserAgent(String keyword) {
    this.keyword = keyword;
    this.enabled = true;
  }

  public String getKeyword() { return keyword; }
  public void setKeyword(String keyword) { this.keyword = keyword; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

}