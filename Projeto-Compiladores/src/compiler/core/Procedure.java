package compiler.core;

import java.util.ArrayList;
import java.util.List;


public class Procedure extends ScopedEntity{

	List<Parameter> params;
	
	public Procedure(String name, ArrayList<Parameter>params){
        super(name);
        if(params != null){
			this.params = params;
		}else{
			this.params = new ArrayList<Parameter>();
		}
	}

	public List<Parameter> getParams() {
		return this.params;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Procedure)) return false;
		Procedure f= (Procedure) obj;
		if(!f.getName().equals(getName()))return false;
		if(f.getParams().size() != getParams().size()) return false;
		
		for(int i=0;i<getParams().size();i++){
			if(! f.getParams().get(i).getType().equals(getParams().get(i).getType())) return false;
		}
		
		return true;
	}


}
