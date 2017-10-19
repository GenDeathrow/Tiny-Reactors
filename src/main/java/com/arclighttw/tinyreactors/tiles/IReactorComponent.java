package com.arclighttw.tinyreactors.tiles;

public interface IReactorComponent
{
	TileEntityReactorController getController();
	void setController(TileEntityReactorController controller);
	
	void invalidateController();
}
