'''
Created on 30 Mar 2018

@author: vwltam
modified by ho yu hin on 18/5
'''
import plotly.plotly as py
import pandas as pd
from plotly.graph_objs import *

py.sign_in('ms042087', 'Ytktzy7FUBCOGMoFSX0w')

hb=pd.read_csv("test.csv")

data = Data([
    Scatter(
        y=hb["y"],
        mode='lines+markers',
        name="'linear'",
        hoverinfo='name',
        line=dict(
            shape='linear'
            )
    )
])

layout = Layout(
    title='HeartBeat [Line Chart]',
    xaxis = dict(title = 'data'),
    yaxis = dict(title = 'HeartBeat rate'),
    font=Font(
        family='Courier'
    )
)

fig = Figure(data=data, layout=layout)
plot_url = py.plot(data,filename='HeartBeat [Line]')
py.image.save_as(fig, 'HeartBeat.png')
