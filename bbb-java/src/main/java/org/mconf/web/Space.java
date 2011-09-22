package org.mconf.web;

public class Space extends Owner {
	protected String name;
	protected boolean public_, member;
	
	@Override
	public String getType() {
		return Owner.TYPE_SPACE;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPublic() {
		return public_;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPublic(boolean public_) {
		this.public_ = public_;
	}

	public boolean isMember() {
		return member;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	@Override
	public String toString() {
		return super.toString() + ", name: " + name + ", public? " + public_ + ", member? " + member;
	}

}
