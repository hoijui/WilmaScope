wilma.ps: wilma.dvi
	dvips wilma.dvi -o wilma.ps
	gv wilma.ps &

wilma.dvi: wilma.tex
	latex wilma || (bibtex wilma && /bin/false) # make sure bibtex is always run
	bibtex wilma
	latex wilma
	latex wilma

clean:
	rm wilma.ps *.dvi *.log *.idx *.aux
