wilma.ps: wilma.dvi
	dvips wilma.dvi -o wilma.ps
	gv wilma.ps &

wilma.dvi: wilma.tex
	latex wilma
	latex wilma
	bibtex wilma
	latex wilma

clean:
	rm wilma.ps *.dvi *.log *.idx *.aux
