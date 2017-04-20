package com.estafet.openshift.dm.model.entity;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.estafet.openshift.dm.config.Constants.*;

/**
 * Created by Delcho Delov on 9.3.2017 г..
 *
 */
@Entity
@Table(name = TABLE_NAME_DEVICE_OWNERSHIP, schema = SCHEMA_NAME)
public class DeviceOwnership {
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id = INVALID_ID;

		@Column(name = COL_CUST_ID, nullable = false)
		private final String customerId;// customer email for the moment

		@Column(name = COL_THING_NAME, nullable = false)
		private final String thingName;
		@Column(name = COL_THING_TYPE, nullable = false)
		private final String thingTypeName;
		@Column(name = COL_SN, nullable = false)
		private final String sn;
		@Column(name = COL_OWN, nullable = false)
		private final boolean own;
		@Column(name = COL_VALID_FROM, nullable = false)
		private final String validFrom; // date in format 'yyyyMMdd'
		@Column(name = COL_VALID_TO)
		private String validTo; // date in format 'yyyyMMdd'

		/**
		 * Constructs a new object from scratch - suitable for DB insert operation
		 *
		 * @param customerId
		 * @param thingName
		 * @param thingTypeName
		 * @param sn
		 * @param own
		 * @param validFrom
		 */
		public DeviceOwnership(String customerId, String thingName, String thingTypeName, String sn, boolean own, String validFrom) {
				this.customerId = customerId;
				this.thingName = thingName;
				this.thingTypeName = thingTypeName;
				this.sn = sn;
				this.own = own;
				this.validFrom = validFrom;
		}

		/**
		 * Constructor suitable for DB loaded record
		 */
		public DeviceOwnership(int id, String customerId, String thingName, String thingTypeName, String sn, boolean own, String validFrom, String validTo) {
				this(customerId, thingName, thingTypeName, sn, own, validFrom);
				this.id = id;
				this.validTo = validTo;
		}

		public int getId() {
				return id;
		}

		public String getCustomerId() {
				return customerId;
		}

		public String getThingName() {
				return thingName;
		}

		public boolean isOwn() {
				return own;
		}

		public String getValidFrom() {
				return validFrom;
		}

		public String getValidTo() {
				return validTo;
		}

		public void setValidTo(Calendar calendar) {
				if (calendar != null) {
						final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
						sdf.setTimeZone(calendar.getTimeZone());
						this.validTo = sdf.format(calendar.getTime());
				}
		}

		public String getThingTypeName() {
				return thingTypeName;
		}

		public String getSn() {
				return sn;
		}

		public Map<String, Object> asMap() {
				Map<String, Object> res = new HashMap<>(8);
				res.put(COL_ID, getId());
				res.put(COL_CUST_ID, getCustomerId());
				res.put(COL_THING_NAME, getThingName());
				res.put(COL_THING_TYPE, getThingTypeName());
				res.put(COL_SN, getSn());
				res.put(COL_OWN, isOwn());
				res.put(COL_VALID_FROM, getValidFrom());
				res.put(COL_VALID_TO, getValidTo());
				return res;
		}

		@Override
		public String toString() {
				return "DeviceOwnership{" +
								"id=" + id +
								", customerId='" + customerId + '\'' +
								", thingName='" + thingName + '\'' +
								", thingTypeName='" + thingTypeName + '\'' +
								", sn='" + sn + '\'' +
								", own=" + own +
								", validFrom='" + validFrom + '\'' +
								", validTo='" + validTo + '\'' +
								'}';
		}
}
