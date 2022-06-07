package tuni.saatiedot.ilmatieteenlaitos.datatypes.gml;

/**
 * 
 * 
 */
public class Position {
	private Double _latitude = null;
	private Double _longitude = null;
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public Position(Double latitude, Double longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}
	
	/**
	 * for serialization
	 */
	public Position() {
		// nothing needed
	}

	/**
	 * 
	 * @return latitude
	 */
	public Double getLatitude() {
		return _latitude;
	}

	/**
	 * 
	 * @return longitude
	 */
	public Double getLongitude() {
		return _longitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_latitude == null) ? 0 : _latitude.hashCode());
		result = prime * result + ((_longitude == null) ? 0 : _longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (_latitude == null) {
			if (other._latitude != null)
				return false;
		} else if (!_latitude.equals(other._latitude))
			return false;
		if (_longitude == null) {
			if (other._longitude != null)
				return false;
		} else if (!_longitude.equals(other._longitude))
			return false;
		return true;
	}
}
