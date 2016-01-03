package textmining;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NVDEntry extends Entry{

	private TextData date;
	private TextData description;
	private TextData product;
	private double rating;
	
	
	public NVDEntry(TextData date, TextData description, TextData product,
			double rating) {
		this.date = date;
		this.description = description;
		this.product = product;
		this.rating = rating;
		super.process();
	}
	
	public NVDEntry() {	}
	public TextData getDate() {
		return date;
	}
	public TextData getDescription() {
		return description;
	}
	public TextData getProduct() {
		return product;
	}
	public double getRating() {
		return rating;
	}
	public void setDate(TextData date) {
		this.date = date;
	}
	public void setDescription(TextData description) {
		this.description = description;
	}
	public void setProduct(TextData product) {
		this.product = product;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	protected Set<TextData> getAllSummableFields() {
		Set<TextData> fields= new HashSet<TextData>();
		fields.add(description);
		fields.add(product);
		return fields;
	}

	@Override
	protected Set<TextData> getAllFilterFields() {
		Set<TextData> fields= new HashSet<TextData>();
		fields.add(date);
		return fields;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		long temp;
		temp = Double.doubleToLongBits(rating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		NVDEntry other = (NVDEntry) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (Double.doubleToLongBits(rating) != Double
				.doubleToLongBits(other.rating))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return date.getData() + "^" + description.getData()
				+ "^" + product.getData() + "^" + rating;
	}
	
	
}
