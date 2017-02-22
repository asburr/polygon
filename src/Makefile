
OBJ=../obj

all: $(OBJ)/Rectangle.class $(OBJ)/Polygon.class $(OBJ)/Polygons.class

clean:
	rm $(OBJ)/*.class


$(OBJ)/%.class: %.java
	javac -d $(OBJ) $<
