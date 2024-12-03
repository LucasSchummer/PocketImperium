package pimperium.elements;

public class CentralSector extends Sector{
	
	//Testing commit
	public CentralSector() {
		this.systems.add(new HSystem(3));
		this.path = "center.png";
	}

	@Override
	public boolean isTriPrime() {
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
