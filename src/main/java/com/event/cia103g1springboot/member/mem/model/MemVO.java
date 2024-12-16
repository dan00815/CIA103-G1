package com.event.cia103g1springboot.member.mem.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import com.event.cia103g1springboot.event.evtordermodel.EvtOrderVO;
import com.event.cia103g1springboot.member.notify.model.MemberNotifyVO;
import com.event.cia103g1springboot.plan.planorder.model.PlanOrder;
import com.event.cia103g1springboot.product.productorder.model.ProductOrderVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "memId")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "member")
public class MemVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public interface LoginGroup {
	}

	public interface ModPwd {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "memid", updatable = false, insertable = false)
	private Integer memId;

	@Column(name = "name")
	@NotEmpty(message = "名稱不可空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "姓名僅能由中、英文大小寫組成，長度需介於2-10個字之間")
	private String name;

	@Column(name = "birth")
	@NotNull(message = "生日不可空白")
	private Date birth;

	@Column(name = "sex")
	private Integer sex;

	@Column(name = "memacct", updatable = false)
	@NotEmpty(message = "帳號不可空白", groups = { LoginGroup.class })
	@Size(min = 3, max = 30, message = "帳號長度需在3-20字之間")
	@Pattern(regexp = "^[a-zA-Z\\d]+$", message = "帳號僅能由大小寫英文字母、數字組成，不能含有特殊符號")
	private String memAcct;

	@Column(name = "mempwd")
	@NotEmpty(message = "密碼不可空白", groups = { LoginGroup.class })
	@Size(min = 3, max = 20, message = "密碼長度需在3-30字之間", groups = { Default.class, ModPwd.class })
	@Pattern(regexp = "^(?=.*[A-Z])[A-Za-z\\d]+$", message = "密碼僅能由大小寫英文+數字組成且至少須有一個大寫字母", groups = { Default.class,
			ModPwd.class })
	private String memPwd;

	@Column(name = "email")
	@NotEmpty(message = "信箱不可空白")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "信箱格式有誤，請重新確認")
	private String email;

	@Column(name = "tel")
	@NotEmpty(message = "電話不可空白")
	@Pattern(regexp = "^09\\d{8}$", message = "電話格式有誤，請重新確認")
	private String tel;

	@Column(name = "addr")
	@Pattern(regexp = "^[\\u4e00-\\u9fa5\\d]+$", message = "地址格式有誤")
	@NotEmpty(message = "居住地址不可空白")
	private String addr;

	@Column(name = "regdate", updatable = false, insertable = false)
	private Timestamp regDate;

	@Column(name = "memtype", updatable = false)
	private Integer memType;

	@Column(name = "memimg", nullable = true)

	private byte[] memImg;

	@Column(name = "memstat", insertable = false)
	private Integer memStat = 1;

	@Transient
	Function<Integer, String> genderConvert = genderCode -> {
		switch (genderCode) {
		case 1:
			return "男";
		case 2:
			return "女";
		default:
			return "其他";
		}
	};

	public String getGenderText() {
		return genderConvert.apply(sex);
	}

	@Override
	public String toString() {
		return "MemVO [memId=" + memId + ", name=" + name + ", birth=" + birth + ", sex=" + sex + ", memAcct=" + memAcct
				+ ", memPwd=" + memPwd + ", email=" + email + ", tel=" + tel + ", addr=" + addr + ", regDate=" + regDate
				+ ", memType=" + memType + ", memImg=" + Arrays.toString(memImg) + ", memStat=" + memStat + "]";
	}

	@OneToMany(mappedBy = "memVO", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EvtOrderVO> evtOrders;

	@OneToMany(mappedBy = "memVO", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<PlanOrder> planOrder;

	@OneToMany(mappedBy = "memVO", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ProductOrderVO> productOrderVO;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<MemberNotifyVO> memberNotifyVO;
}
