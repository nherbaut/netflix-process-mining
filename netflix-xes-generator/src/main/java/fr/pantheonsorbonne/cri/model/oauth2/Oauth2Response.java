package fr.pantheonsorbonne.cri.model.oauth2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Oauth2Response {

	public Oauth2Response() {

	}

	@XmlAttribute(name = "access_token")
	public String access_token;
	@XmlAttribute(name = "expires_in")
	public Long expires_in;
	@XmlAttribute(name = "refresh_expires_in")
	public Long refresh_expires_in;
	@XmlAttribute(name = "refresh_token")
	public String refresh_token;
	@XmlAttribute(name = "token_type")
	public String token_type;
	@XmlAttribute(name = "not-before-policy")
	public String not_before_policy;
	@XmlAttribute(name = "session_state")
	public String session_state;
	@XmlAttribute(name = "scope")
	public String scope;

}